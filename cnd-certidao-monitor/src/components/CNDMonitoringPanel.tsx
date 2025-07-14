
import React, { useState, useEffect } from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Checkbox } from '@/components/ui/checkbox';
import { Badge } from '@/components/ui/badge';
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from '@/components/ui/tooltip';
import { AlertDialog, AlertDialogAction, AlertDialogCancel, AlertDialogContent, AlertDialogDescription, AlertDialogFooter, AlertDialogHeader, AlertDialogTitle, AlertDialogTrigger } from '@/components/ui/alert-dialog';
import { 
  Download, 
  Edit, 
  Trash2, 
  Clock, 
  Search, 
  FileDown, 
  Plus, 
  CheckCircle, 
  XCircle, 
  AlertCircle,
  Filter
} from 'lucide-react';
import { format } from 'date-fns';
import { ptBR } from 'date-fns/locale';
import { toast } from 'sonner';
import EditRecordModal from './EditRecordModal';
import HistoryModal from './HistoryModal';

// Types
interface CNDRecord {
  id: string;
  cnpj: string;
  nomeContribuinte: string;
  tipo: 'FED' | 'EST' | 'MUN';
  orgaoEmissor: string;
  situacaoCertidao: 'Negativa' | 'Positiva com Efeitos de Negativa' | 'Positiva';
  dataEmissao: string;
  dataValidade: string;
  statusProcessamento: 'Concluído' | 'Pendente' | 'Erro' | 'Emissor Indisponível';
  dataProcessamento: string;
  codigoControle: string;
  arquivoDisponivel: boolean;
  detalhesErro?: string;
}

interface Filters {
  cnpj: string;
  nomeContribuinte: string;
  situacaoCertidao: string;
  statusProcessamento: string;
  tiposCertidao: string[];
}

const CNDMonitoringPanel: React.FC = () => {
  const [records, setRecords] = useState<CNDRecord[]>([]);
  const [filteredRecords, setFilteredRecords] = useState<CNDRecord[]>([]);
  const [selectedRecords, setSelectedRecords] = useState<string[]>([]);
  const [loading, setLoading] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [showFilters, setShowFilters] = useState(false);
  const [editModalOpen, setEditModalOpen] = useState(false);
  const [editingRecord, setEditingRecord] = useState<CNDRecord | null>(null);
  const [historyModalOpen, setHistoryModalOpen] = useState(false);
  const [historyRecord, setHistoryRecord] = useState<{cnpj: string, tipo: string} | null>(null);
  const recordsPerPage = 10;

  const [filters, setFilters] = useState<Filters>({
    cnpj: '',
    nomeContribuinte: '',
    situacaoCertidao: '',
    statusProcessamento: '',
    tiposCertidao: [],
  });

  // Mock data - replace with actual API calls
  const mockData: CNDRecord[] = [
    {
      id: '1',
      cnpj: '12.345.678/0001-90',
      nomeContribuinte: 'Empresa ABC Ltda',
      tipo: 'FED',
      orgaoEmissor: 'Receita Federal',
      situacaoCertidao: 'Negativa',
      dataEmissao: '2024-01-15',
      dataValidade: '2024-07-15',
      statusProcessamento: 'Concluído',
      dataProcessamento: '2024-01-16',
      codigoControle: 'ABC123456789DEF',
      arquivoDisponivel: true,
    },
    {
      id: '2',
      cnpj: '98.765.432/0001-10',
      nomeContribuinte: 'Comércio XYZ S.A.',
      tipo: 'EST',
      orgaoEmissor: 'SEFAZ-SP',
      situacaoCertidao: 'Positiva com Efeitos de Negativa',
      dataEmissao: '2024-02-10',
      dataValidade: '2024-08-10',
      statusProcessamento: 'Pendente',
      dataProcessamento: '2024-02-11',
      codigoControle: 'XYZ987654321GHI',
      arquivoDisponivel: false,
    }
  ];

  useEffect(() => {
    // Simulate API call
    setLoading(true);
    setTimeout(() => {
      setRecords(mockData);
      setFilteredRecords(mockData);
      setLoading(false);
    }, 1000);
  }, []);

  // CNPJ mask function
  const formatCNPJ = (value: string) => {
    return value
      .replace(/\D/g, '')
      .replace(/^(\d{2})(\d)/, '$1.$2')
      .replace(/^(\d{2})\.(\d{3})(\d)/, '$1.$2.$3')
      .replace(/\.(\d{3})(\d)/, '.$1/$2')
      .replace(/(\d{4})(\d)/, '$1-$2')
      .substring(0, 18);
  };

  const handleCNPJChange = (value: string) => {
    const formatted = formatCNPJ(value);
    setFilters(prev => ({ ...prev, cnpj: formatted }));
  };

  const getSituacaoBadgeColor = (situacao: string) => {
    switch (situacao) {
      case 'Negativa': return 'bg-green-100 text-green-800 border-green-200';
      case 'Positiva com Efeitos de Negativa': return 'bg-yellow-100 text-yellow-800 border-yellow-200';
      case 'Positiva': return 'bg-red-100 text-red-800 border-red-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getTipoBadgeColor = (tipo: string) => {
    switch (tipo) {
      case 'FED': return 'bg-blue-100 text-blue-800 border-blue-200';
      case 'EST': return 'bg-purple-100 text-purple-800 border-purple-200';
      case 'MUN': return 'bg-orange-100 text-orange-800 border-orange-200';
      default: return 'bg-gray-100 text-gray-800 border-gray-200';
    }
  };

  const getStatusIcon = (status: string, detalhes?: string) => {
    switch (status) {
      case 'Concluído':
        return <CheckCircle className="h-4 w-4 text-green-600" />;
      case 'Pendente':
        return <AlertCircle className="h-4 w-4 text-yellow-600" />;
      case 'Erro':
      case 'Emissor Indisponível':
        return (
          <TooltipProvider>
            <Tooltip>
              <TooltipTrigger>
                <XCircle className="h-4 w-4 text-red-600" />
              </TooltipTrigger>
              <TooltipContent>
                <p>{detalhes || 'Erro no processamento'}</p>
              </TooltipContent>
            </Tooltip>
          </TooltipProvider>
        );
      default:
        return null;
    }
  };

  const isExpiringSoon = (dataValidade: string) => {
    try {
      const today = new Date();
      const validadeDate = new Date(dataValidade);
      const diffTime = validadeDate.getTime() - today.getTime();
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      return diffDays <= 30 && diffDays > 0;
    } catch {
      return false;
    }
  };

  const isExpired = (dataValidade: string) => {
    try {
      const today = new Date();
      const validadeDate = new Date(dataValidade);
      return validadeDate < today;
    } catch {
      return false;
    }
  };

  const handleSelectRecord = (recordId: string) => {
    setSelectedRecords(prev => 
      prev.includes(recordId) 
        ? prev.filter(id => id !== recordId)
        : [...prev, recordId]
    );
  };

  const handleSelectAll = () => {
    const currentPageRecords = getCurrentPageRecords();
    const allSelected = currentPageRecords.every(record => selectedRecords.includes(record.id));
    
    if (allSelected) {
      setSelectedRecords(prev => prev.filter(id => !currentPageRecords.some(record => record.id === id)));
    } else {
      setSelectedRecords(prev => [...new Set([...prev, ...currentPageRecords.map(record => record.id)])]);
    }
  };

  const handleDownload = async (recordId: string) => {
    try {
      // API call to download PDF
      toast.success('Download iniciado com sucesso!');
    } catch (error) {
      toast.error('Erro ao fazer download do arquivo');
    }
  };

  const handleEdit = (record: CNDRecord) => {
    setEditingRecord(record);
    setEditModalOpen(true);
  };

  const handleSaveEdit = (updatedRecord: CNDRecord) => {
    try {
      setRecords(prev => prev.map(record => 
        record.id === updatedRecord.id ? updatedRecord : record
      ));
      setFilteredRecords(prev => prev.map(record => 
        record.id === updatedRecord.id ? updatedRecord : record
      ));
    } catch (error) {
      toast.error('Erro ao salvar alterações');
    }
  };

  const handleShowHistory = (record: CNDRecord) => {
    setHistoryRecord({ cnpj: record.cnpj, tipo: record.tipo });
    setHistoryModalOpen(true);
  };

  const handleDelete = async (recordId: string) => {
    try {
      // API call to delete record
      setRecords(prev => prev.filter(record => record.id !== recordId));
      setFilteredRecords(prev => prev.filter(record => record.id !== recordId));
      toast.success('Registro excluído com sucesso!');
    } catch (error) {
      toast.error('Erro ao excluir registro');
    }
  };

  const handleBulkDelete = async () => {
    try {
      // API call to delete multiple records
      setRecords(prev => prev.filter(record => !selectedRecords.includes(record.id)));
      setFilteredRecords(prev => prev.filter(record => !selectedRecords.includes(record.id)));
      setSelectedRecords([]);
      toast.success(`${selectedRecords.length} registros excluídos com sucesso!`);
    } catch (error) {
      toast.error('Erro ao excluir registros selecionados');
    }
  };

  const applyFilters = () => {
    try {
      let filtered = [...records];

      if (filters.cnpj) {
        filtered = filtered.filter(record => 
          record.cnpj.includes(filters.cnpj.replace(/\D/g, ''))
        );
      }

      if (filters.nomeContribuinte) {
        filtered = filtered.filter(record =>
          record.nomeContribuinte.toLowerCase().includes(filters.nomeContribuinte.toLowerCase())
        );
      }

      if (filters.situacaoCertidao) {
        filtered = filtered.filter(record => record.situacaoCertidao === filters.situacaoCertidao);
      }

      if (filters.statusProcessamento) {
        filtered = filtered.filter(record => record.statusProcessamento === filters.statusProcessamento);
      }

      if (filters.tiposCertidao.length > 0) {
        filtered = filtered.filter(record => filters.tiposCertidao.includes(record.tipo));
      }

      setFilteredRecords(filtered);
      setCurrentPage(1);
      toast.success('Filtros aplicados com sucesso!');
    } catch (error) {
      toast.error('Erro ao aplicar filtros');
    }
  };

  const clearFilters = () => {
    try {
      setFilters({
        cnpj: '',
        nomeContribuinte: '',
        situacaoCertidao: '',
        statusProcessamento: '',
        tiposCertidao: [],
      });
      setFilteredRecords(records);
      setCurrentPage(1);
      toast.success('Filtros limpos com sucesso!');
    } catch (error) {
      toast.error('Erro ao limpar filtros');
    }
  };

  const getCurrentPageRecords = () => {
    const startIndex = (currentPage - 1) * recordsPerPage;
    const endIndex = startIndex + recordsPerPage;
    return filteredRecords.slice(startIndex, endIndex);
  };

  const totalPages = Math.ceil(filteredRecords.length / recordsPerPage);

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Header */}
      <div className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex justify-between items-center py-4">
            <div className="flex items-center space-x-4">
              <img 
                src="/lovable-uploads/c8ea87eb-0bbe-4a2e-b4af-0266e161e2bd.png" 
                alt="Logo da Empresa" 
                className="h-12 w-auto"
              />
              <h1 className="text-2xl font-bold text-primary">
                Monitoramento de Certidões (CND)
              </h1>
            </div>
          </div>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
        {/* Filters */}
        <Card className="mb-6">
          <CardHeader>
            <div className="flex justify-between items-center">
              <CardTitle className="text-lg flex items-center space-x-2">
                <Filter className="h-5 w-5" />
                <span>Filtros de Pesquisa</span>
              </CardTitle>
              <Button
                variant="outline"
                size="sm"
                onClick={() => setShowFilters(!showFilters)}
              >
                {showFilters ? 'Ocultar' : 'Mostrar'} Filtros
              </Button>
            </div>
          </CardHeader>
          
          {showFilters && (
            <CardContent>
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4 mb-4">
                <div>
                  <Label htmlFor="cnpj">CNPJ do Cliente</Label>
                  <Input
                    id="cnpj"
                    value={filters.cnpj}
                    onChange={(e) => handleCNPJChange(e.target.value)}
                    placeholder="00.000.000/0000-00"
                  />
                </div>
                
                <div>
                  <Label htmlFor="nome">Nome do Contribuinte</Label>
                  <Input
                    id="nome"
                    value={filters.nomeContribuinte}
                    onChange={(e) => setFilters(prev => ({ ...prev, nomeContribuinte: e.target.value }))}
                    placeholder="Digite o nome"
                  />
                </div>

                <div>
                  <Label>Situação da Certidão</Label>
                  <Select
                    value={filters.situacaoCertidao}
                    onValueChange={(value) => setFilters(prev => ({ ...prev, situacaoCertidao: value }))}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione a situação" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="todas">Todas</SelectItem>
                      <SelectItem value="Negativa">Negativa</SelectItem>
                      <SelectItem value="Positiva com Efeitos de Negativa">Positiva com Efeitos de Negativa</SelectItem>
                      <SelectItem value="Positiva">Positiva</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div>
                  <Label>Status do Processamento</Label>
                  <Select
                    value={filters.statusProcessamento}
                    onValueChange={(value) => setFilters(prev => ({ ...prev, statusProcessamento: value }))}
                  >
                    <SelectTrigger>
                      <SelectValue placeholder="Selecione o status" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="todos">Todos</SelectItem>
                      <SelectItem value="Concluído">Concluído</SelectItem>
                      <SelectItem value="Pendente">Pendente</SelectItem>
                      <SelectItem value="Erro">Erro</SelectItem>
                      <SelectItem value="Emissor Indisponível">Emissor Indisponível</SelectItem>
                    </SelectContent>
                  </Select>
                </div>

                <div className="md:col-span-2">
                  <Label>Tipo de Certidão</Label>
                  <div className="flex space-x-4 mt-2">
                    {['FED', 'EST', 'MUN'].map((tipo) => (
                      <div key={tipo} className="flex items-center space-x-2">
                        <Checkbox
                          id={tipo}
                          checked={filters.tiposCertidao.includes(tipo)}
                          onCheckedChange={(checked) => {
                            if (checked) {
                              setFilters(prev => ({
                                ...prev,
                                tiposCertidao: [...prev.tiposCertidao, tipo]
                              }));
                            } else {
                              setFilters(prev => ({
                                ...prev,
                                tiposCertidao: prev.tiposCertidao.filter(t => t !== tipo)
                              }));
                            }
                          }}
                        />
                        <Label htmlFor={tipo}>{tipo === 'FED' ? 'Federal' : tipo === 'EST' ? 'Estadual' : 'Municipal'}</Label>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="flex space-x-2">
                <Button onClick={applyFilters} className="bg-primary hover:bg-primary/90">
                  <Search className="mr-2 h-4 w-4" />
                  Aplicar Filtros
                </Button>
                <Button variant="outline" onClick={clearFilters}>
                  Limpar Filtros
                </Button>
              </div>
            </CardContent>
          )}
        </Card>

        {/* Action Buttons */}
        <div className="flex flex-wrap gap-2 mb-6">
          <Button className="bg-primary hover:bg-primary/90">
            <Plus className="mr-2 h-4 w-4" />
            Nova Consulta
          </Button>
          <Button variant="outline">
            <FileDown className="mr-2 h-4 w-4" />
            Exportar
          </Button>
          {selectedRecords.length > 0 && (
            <AlertDialog>
              <AlertDialogTrigger asChild>
                <Button variant="destructive">
                  <Trash2 className="mr-2 h-4 w-4" />
                  Excluir Selecionados ({selectedRecords.length})
                </Button>
              </AlertDialogTrigger>
              <AlertDialogContent>
                <AlertDialogHeader>
                  <AlertDialogTitle>Confirmar Exclusão</AlertDialogTitle>
                  <AlertDialogDescription>
                    Tem certeza que deseja excluir {selectedRecords.length} registro(s)? Esta ação não pode ser desfeita.
                  </AlertDialogDescription>
                </AlertDialogHeader>
                <AlertDialogFooter>
                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                  <AlertDialogAction onClick={handleBulkDelete} className="bg-red-600 hover:bg-red-700">
                    Excluir
                  </AlertDialogAction>
                </AlertDialogFooter>
              </AlertDialogContent>
            </AlertDialog>
          )}
        </div>

        {/* Results Table */}
        <Card>
          <CardContent className="p-0">
            <div className="overflow-x-auto">
              <table className="w-full">
                <thead className="bg-gray-50 border-b">
                  <tr>
                    <th className="px-4 py-3 text-left">
                      <Checkbox
                        checked={getCurrentPageRecords().length > 0 && getCurrentPageRecords().every(record => selectedRecords.includes(record.id))}
                        onCheckedChange={handleSelectAll}
                      />
                    </th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">CNPJ</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Nome</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Tipo</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Órgão</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Situação</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Validade</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Status</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Código</th>
                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-900">Ações</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {loading ? (
                    <tr>
                      <td colSpan={10} className="px-4 py-8 text-center text-gray-500">
                        Carregando...
                      </td>
                    </tr>
                  ) : getCurrentPageRecords().length === 0 ? (
                    <tr>
                      <td colSpan={10} className="px-4 py-8 text-center text-gray-500">
                        Nenhum registro encontrado
                      </td>
                    </tr>
                  ) : (
                    getCurrentPageRecords().map((record) => (
                      <tr key={record.id} className="hover:bg-gray-50">
                        <td className="px-4 py-3">
                          <Checkbox
                            checked={selectedRecords.includes(record.id)}
                            onCheckedChange={() => handleSelectRecord(record.id)}
                          />
                        </td>
                        <td className="px-4 py-3 text-sm font-mono">{record.cnpj}</td>
                        <td className="px-4 py-3 text-sm">{record.nomeContribuinte}</td>
                        <td className="px-4 py-3">
                          <Badge className={getTipoBadgeColor(record.tipo)}>
                            {record.tipo}
                          </Badge>
                        </td>
                        <td className="px-4 py-3 text-sm">{record.orgaoEmissor}</td>
                        <td className="px-4 py-3">
                          <Badge className={getSituacaoBadgeColor(record.situacaoCertidao)}>
                            {record.situacaoCertidao}
                          </Badge>
                        </td>
                        <td className="px-4 py-3 text-sm">
                          <span className={`${isExpired(record.dataValidade) ? 'text-red-600 font-semibold' : 
                            isExpiringSoon(record.dataValidade) ? 'text-yellow-600 font-semibold' : ''}`}>
                            {format(new Date(record.dataValidade), 'dd/MM/yyyy', { locale: ptBR })}
                          </span>
                        </td>
                        <td className="px-4 py-3">
                          {getStatusIcon(record.statusProcessamento, record.detalhesErro)}
                        </td>
                        <td className="px-4 py-3 text-sm font-mono">
                          <TooltipProvider>
                            <Tooltip>
                              <TooltipTrigger>
                                {record.codigoControle.substring(0, 8)}...
                              </TooltipTrigger>
                              <TooltipContent>
                                <p>{record.codigoControle}</p>
                              </TooltipContent>
                            </Tooltip>
                          </TooltipProvider>
                        </td>
                        <td className="px-4 py-3">
                          <div className="flex space-x-2">
                            <TooltipProvider>
                              <Tooltip>
                                <TooltipTrigger asChild>
                                  <Button
                                    size="sm"
                                    variant="outline"
                                    onClick={() => handleDownload(record.id)}
                                    disabled={!record.arquivoDisponivel}
                                  >
                                    <Download className="h-4 w-4" />
                                  </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                  <p>Download PDF</p>
                                </TooltipContent>
                              </Tooltip>
                            </TooltipProvider>

                            <TooltipProvider>
                              <Tooltip>
                                <TooltipTrigger asChild>
                                  <Button 
                                    size="sm" 
                                    variant="outline"
                                    onClick={() => handleEdit(record)}
                                  >
                                    <Edit className="h-4 w-4" />
                                  </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                  <p>Editar</p>
                                </TooltipContent>
                              </Tooltip>
                            </TooltipProvider>

                            <TooltipProvider>
                              <Tooltip>
                                <TooltipTrigger asChild>
                                  <Button 
                                    size="sm" 
                                    variant="outline"
                                    onClick={() => handleShowHistory(record)}
                                  >
                                    <Clock className="h-4 w-4" />
                                  </Button>
                                </TooltipTrigger>
                                <TooltipContent>
                                  <p>Histórico</p>
                                </TooltipContent>
                              </Tooltip>
                            </TooltipProvider>

                            <AlertDialog>
                              <AlertDialogTrigger asChild>
                                <TooltipProvider>
                                  <Tooltip>
                                    <TooltipTrigger asChild>
                                      <Button size="sm" variant="outline" className="text-red-600 hover:bg-red-50">
                                        <Trash2 className="h-4 w-4" />
                                      </Button>
                                    </TooltipTrigger>
                                    <TooltipContent>
                                      <p>Excluir</p>
                                    </TooltipContent>
                                  </Tooltip>
                                </TooltipProvider>
                              </AlertDialogTrigger>
                              <AlertDialogContent>
                                <AlertDialogHeader>
                                  <AlertDialogTitle>Confirmar Exclusão</AlertDialogTitle>
                                  <AlertDialogDescription>
                                    Tem certeza que deseja excluir este registro? Esta ação não pode ser desfeita.
                                  </AlertDialogDescription>
                                </AlertDialogHeader>
                                <AlertDialogFooter>
                                  <AlertDialogCancel>Cancelar</AlertDialogCancel>
                                  <AlertDialogAction 
                                    onClick={() => handleDelete(record.id)}
                                    className="bg-red-600 hover:bg-red-700"
                                  >
                                    Excluir
                                  </AlertDialogAction>
                                </AlertDialogFooter>
                              </AlertDialogContent>
                            </AlertDialog>
                          </div>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            {totalPages > 1 && (
              <div className="flex items-center justify-between px-4 py-3 border-t">
                <div className="text-sm text-gray-700">
                  Mostrando {((currentPage - 1) * recordsPerPage) + 1} a {Math.min(currentPage * recordsPerPage, filteredRecords.length)} de {filteredRecords.length} registros
                </div>
                <div className="flex space-x-2">
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))}
                    disabled={currentPage === 1}
                  >
                    Anterior
                  </Button>
                  <span className="px-3 py-1 text-sm">
                    Página {currentPage} de {totalPages}
                  </span>
                  <Button
                    variant="outline"
                    size="sm"
                    onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))}
                    disabled={currentPage === totalPages}
                  >
                    Próxima
                  </Button>
                </div>
              </div>
            )}
          </CardContent>
        </Card>
      </div>

      {/* Modals */}
      <EditRecordModal
        isOpen={editModalOpen}
        onClose={() => {
          setEditModalOpen(false);
          setEditingRecord(null);
        }}
        record={editingRecord}
        onSave={handleSaveEdit}
      />

      <HistoryModal
        isOpen={historyModalOpen}
        onClose={() => {
          setHistoryModalOpen(false);
          setHistoryRecord(null);
        }}
        cnpj={historyRecord?.cnpj || ''}
        tipo={historyRecord?.tipo || ''}
      />
    </div>
  );
};

export default CNDMonitoringPanel;
